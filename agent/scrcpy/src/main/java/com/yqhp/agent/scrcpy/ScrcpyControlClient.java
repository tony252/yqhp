package com.yqhp.agent.scrcpy;

import com.android.ddmlib.IDevice;
import com.yqhp.agent.scrcpy.message.KeyEvent;
import com.yqhp.agent.scrcpy.message.Position;
import com.yqhp.agent.scrcpy.message.ScrollEvent;
import com.yqhp.agent.scrcpy.message.TouchEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author jiangyitao
 */
@Slf4j
public class ScrcpyControlClient {

    private static final byte INJECT_KEYCODE = 0;
    private static final byte INJECT_TOUCH_EVENT = 2;
    private static final byte INJECT_SCROLL_EVENT = 3;

    private final ByteBuffer touchEventBuffer = ByteBuffer.allocate(28);
    private final ByteBuffer keycodeBuffer = ByteBuffer.allocate(14);
    private final ByteBuffer scrollEventBuffer = ByteBuffer.allocate(25);

    private Socket controlSocket;
    private OutputStream controlOutputStream;

    private final IDevice iDevice;

    ScrcpyControlClient(IDevice iDevice) {
        this.iDevice = iDevice;
    }

    void connect(int localPort) throws IOException {
        controlSocket = new Socket("127.0.0.1", localPort);
        controlOutputStream = controlSocket.getOutputStream();
        log.info("[{}]connect control socket success", iDevice.getSerialNumber());
    }

    void disconnect() {
        if (controlOutputStream != null) {
            try {
                log.info("[{}]close control output stream", iDevice.getSerialNumber());
                controlOutputStream.close();
            } catch (IOException e) {
                log.warn("close control output stream io err", e);
            }
            controlOutputStream = null;
        }

        if (controlSocket != null) {
            try {
                log.info("[{}]close control socket", iDevice.getSerialNumber());
                controlSocket.close();
            } catch (IOException e) {
                log.warn("close control socket io err", e);
            }
            controlSocket = null;
        }
    }

    public void sendTouchEvent(TouchEvent event) throws IOException {
        touchEventBuffer.rewind();

        touchEventBuffer.put(INJECT_TOUCH_EVENT);
        touchEventBuffer.put(event.getAction());
        touchEventBuffer.putLong(-1L); // pointerId
        setPosition(touchEventBuffer, event.getPosition());
        touchEventBuffer.putShort((short) 0xffff); // pressure
        touchEventBuffer.putInt(1); // buttons 鼠标左键: 1 << 0 | 右键: 1 << 1 | 中键: 1 << 2

        controlOutputStream.write(touchEventBuffer.array());
    }

    public void sendKeyEvent(KeyEvent event) throws IOException {
        keycodeBuffer.rewind();

        keycodeBuffer.put(INJECT_KEYCODE);
        keycodeBuffer.put(event.getAction());
        keycodeBuffer.putInt(event.getCode());
        keycodeBuffer.putInt(0); // repeat先简单处理 https://github.com/Genymobile/scrcpy/issues/1013
        keycodeBuffer.putInt(event.getMetaState());

        controlOutputStream.write(keycodeBuffer.array());
    }

    public void sendScrollEvent(ScrollEvent event) throws IOException {
        scrollEventBuffer.rewind();

        scrollEventBuffer.put(INJECT_SCROLL_EVENT);
        setPosition(scrollEventBuffer, event.getPosition());
        scrollEventBuffer.putInt(event.getDeltaX());
        scrollEventBuffer.putInt(event.getDeltaY());
        scrollEventBuffer.putInt(0); // buttons https://github.com/Genymobile/scrcpy/commit/924375487e63a5a9114182015a68aedef2f52d14

        controlOutputStream.write(scrollEventBuffer.array());
    }

    private void setPosition(ByteBuffer buffer, Position position) {
        buffer.putInt(position.getX());
        buffer.putInt(position.getY());
        buffer.putShort((short) position.getWidth());
        buffer.putShort((short) position.getHeight());
    }

}
