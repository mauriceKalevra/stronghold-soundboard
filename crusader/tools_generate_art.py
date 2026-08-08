"""Erzeugt die Pixelgrafiken der App (Hintergrund + Launcher-Icon)."""
from PIL import Image
import math, random, os

RES = "app/src/main/res"

def hexcol(c):
    return tuple(int(c[i:i+2], 16) for i in (1, 3, 5))

class Canvas:
    def __init__(self, w, h, bg="#000000"):
        self.w, self.h = w, h
        self.img = Image.new("RGB", (w, h), hexcol(bg))
    def px(self, x, y, w, h, col, alpha=1.0):
        x, y = round(x), round(y)
        c = hexcol(col)
        for yy in range(y, y + h):
            for xx in range(x, x + w):
                if 0 <= xx < self.w and 0 <= yy < self.h:
                    if alpha >= 1:
                        self.img.putpixel((xx, yy), c)
                    else:
                        b = self.img.getpixel((xx, yy))
                        self.img.putpixel((xx, yy), tuple(int(b[k] * (1 - alpha) + c[k] * alpha) for k in range(3)))
    def disc(self, cx, cy, r, col, alpha=1.0):
        for y in range(-r, r + 1):
            for x in range(-r, r + 1):
                if x * x + y * y <= r * r:
                    self.px(cx + x, cy + y, 1, 1, col, alpha)
    def save(self, path, scale):
        self.img.resize((self.w * scale, self.h * scale), Image.NEAREST).save(path)

# ---------------------------------------------------------------- Hintergrund
def desert():
    W, H = 96, 200
    c = Canvas(W, H)
    rnd = random.Random(20260730)
    HOR = 118
    SKY = ['#131B32','#1C2340','#2A2749','#3F2C4C','#5B3550','#804350','#A65647','#C87241','#E29A4E','#F3C273']
    for y in range(HOR):
        t = y / (HOR - 1) * (len(SKY) - 1)
        i = int(t); f = t - i
        for x in range(W):
            bias = -.25 if (x + y) % 2 == 0 else .25
            c.px(x, y, 1, 1, SKY[min(len(SKY) - 1, i + 1) if f > .5 + bias else i])
    for _ in range(34):
        c.px(rnd.randrange(W), rnd.randrange(54), 1, 1, '#D8D2B6' if rnd.random() < .4 else '#8E8C9C')
    sx, sy = 62, 113
    c.disc(sx, sy, 20, '#FFD9A0', .07)
    c.disc(sx, sy, 14, '#FFDFAC', .09)
    c.disc(sx, sy, 8, '#FFDC98'); c.disc(sx, sy, 6, '#FFEEC4'); c.disc(sx, sy, 3, '#FFF9E4')
    c.px(0, HOR - 4, W, 6, '#F0BE7E', .35)

    def dune(baseY, amp, freq, phase, body, crest, shade):
        prev = None
        for x in range(W):
            y = round(baseY + math.sin(x * freq + phase) * amp + math.sin(x * freq * 2.7 + phase * 1.7) * amp * .35)
            c.px(x, y, 1, H - y, body); c.px(x, y, 1, 2, crest)
            if prev is not None and y > prev and shade:
                c.px(x, y + 2, 1, 5, shade)
            prev = y

    dune(120, 3.5, .09, .4, '#B9834F', '#DCAE72', None)

    bx, gr = 20, 126
    dark, lit, deep = '#5A3A20', '#7C512C', '#3A2412'
    c.px(bx, gr - 10, 26, 10, dark)
    for i in range(0, 26, 4):
        c.px(bx + i, gr - 12, 2, 2, dark)
    c.px(bx + 25, gr - 12, 1, 22, lit)
    c.px(bx - 4, gr - 17, 7, 17, dark); c.px(bx - 4, gr - 19, 2, 2, dark); c.px(bx + 1, gr - 19, 2, 2, dark)
    c.px(bx + 22, gr - 22, 8, 22, dark); c.px(bx + 22, gr - 24, 2, 2, dark); c.px(bx + 26, gr - 24, 2, 2, dark)
    c.px(bx + 29, gr - 22, 1, 22, lit)
    c.px(bx + 11, gr - 6, 4, 6, deep)
    c.px(bx + 30, gr - 30, 1, 8, '#4A2E18'); c.px(bx + 31, gr - 30, 4, 3, '#A8362A')

    dune(133, 5, .075, 2.1, '#9C6338', '#C58A4C', '#7A4A28')
    dune(152, 7, .055, 4.6, '#7C4C2A', '#A9703C', '#5E3720')
    dune(174, 6, .045, 1.2, '#5E3A20', '#8A5730', '#472A16')

    def palm(bx, by, h):
        for i in range(h):
            off = round(math.sin(i / h * 1.3) * 2)
            c.px(bx + off, by - i, 2, 1, '#4E3218' if i % 3 == 0 else '#5C3C1E')
        tx = bx + round(math.sin(1.3) * 2); ty = by - h
        for dx, dy in [(-1, -.75), (1, -.75), (-1.15, -.2), (1.15, -.2), (-.55, -1.05), (.55, -1.05)]:
            for s in range(1, 7):
                c.px(tx + round(dx * s), ty + round(dy * s + s * s * .1), 1, 1, '#2F5C3C' if s < 4 else '#22452D')
        c.px(tx - 1, ty + 2, 1, 1, '#7A3B22'); c.px(tx + 2, ty + 2, 1, 1, '#7A3B22')

    palm(12, 178, 17); palm(21, 183, 12); palm(83, 193, 15)

    for _ in range(220):
        x = rnd.randrange(W); y = 140 + rnd.randrange(60)
        c.px(x, y, 1 + rnd.randrange(3), 1, '#C48C56' if rnd.random() < .5 else '#3A2210', .5)

    os.makedirs(f"{RES}/drawable-nodpi", exist_ok=True)
    c.save(f"{RES}/drawable-nodpi/bg_desert.png", 4)

# --------------------------------------------------------------------- Icon
def icon():
    S = 48
    c = Canvas(S, S)
    SKY = ['#2A2749','#3F2C4C','#5B3550','#804350','#A65647','#C87241','#E29A4E']
    for y in range(30):
        t = y / 29 * (len(SKY) - 1); i = int(t); f = t - i
        for x in range(S):
            bias = -.25 if (x + y) % 2 == 0 else .25
            c.px(x, y, 1, 1, SKY[min(len(SKY) - 1, i + 1) if f > .5 + bias else i])
    c.disc(34, 27, 5, '#FFE3A6')
    for x in range(S):
        y = round(30 + math.sin(x * .18) * 1.5)
        c.px(x, y, 1, S - y, '#A9703C'); c.px(x, y, 1, 2, '#C99257')
    for x in range(S):
        y = round(38 + math.sin(x * .13 + 2) * 2)
        c.px(x, y, 1, S - y, '#7C4C2A'); c.px(x, y, 1, 2, '#9C6338')
    # Turm mit Zinnen und Fahne
    c.px(15, 16, 14, 18, '#5A3A20')
    c.px(27, 16, 2, 18, '#7C512C')
    for i in range(0, 14, 4):
        c.px(15 + i, 13, 3, 3, '#5A3A20')
    c.px(20, 26, 4, 8, '#33200F')
    c.px(21, 5, 1, 9, '#4A2E18')
    c.px(22, 5, 6, 4, '#C4382B')
    for i in range(3):
        c.px(10 + i * 12, 34 + i, 2, 2, '#3A2210', .5)
    for name, scale in [("mipmap-mdpi", 1), ("mipmap-hdpi", 2), ("mipmap-xhdpi", 2), ("mipmap-xxhdpi", 3), ("mipmap-xxxhdpi", 4)]:
        os.makedirs(f"{RES}/{name}", exist_ok=True)
        c.save(f"{RES}/{name}/ic_launcher.png", scale)
        c.save(f"{RES}/{name}/ic_launcher_round.png", scale)

desert()
icon()
print("Grafiken erzeugt")
