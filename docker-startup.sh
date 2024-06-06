# Cleanup to be "stateless" on startup, otherwise pulseaudio daemon can't start
rm -rf /var/run/pulse /var/lib/pulse /root/.config/pulse

# Start pulseaudio as system wide daemon
pulseaudio -D --exit-idle-time=-1 --system --disallow-exit --log-level=0
pactl load-module module-virtual-source master=auto_null.monitor format=s16le source_name=VirtualMic > /dev/null
pactl set-default-source VirtualMic

java -jar ./worker-fatjar.jar