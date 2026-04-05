#!/bin/bash
set -e

# ── VNC password (override via VNC_PASSWORD env var; default: yahtzee) ───────
mkdir -p ~/.vnc
echo "${VNC_PASSWORD:-yahtzee}" | vncpasswd -f > ~/.vnc/passwd
chmod 600 ~/.vnc/passwd

# ── Start a virtual framebuffer on display :1 ───────────────────────────────
Xvfb :1 -screen 0 1280x800x24 &
export DISPLAY=:1

# Wait briefly for Xvfb to start
sleep 1

# ── Start TigerVNC server ───────────────────────────────────────────────────
tigervncserver :1 -geometry 1280x800 -depth 24 -localhost no -PasswordFile ~/.vnc/passwd 2>/dev/null || true

# ── Start noVNC web proxy (browser access at http://host:6080/vnc.html) ─────
websockify --web /usr/share/novnc/ 6080 localhost:5900 &

echo "============================================="
echo "  Yahtzee is starting..."
echo "  VNC direct:  vnc://localhost:5900"
echo "  noVNC web:   http://localhost:6080/vnc.html"
echo "============================================="

# ── Launch the Yahtzee application via jlink launcher ───────────────────────
exec /opt/yahtzee/bin/app -Dyahtzee.data.dir=/data

