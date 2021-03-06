package com.supanadit.restsuite.listener.socket;
import com.supanadit.restsuite.panel.socket.SocketIoPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class SocketIOListenerTableRowMenuListener extends MouseAdapter {
    protected SocketIoPanel panel;

    public SocketIOListenerTableRowMenuListener(SocketIoPanel panel) {
        this.panel = panel;
    }

    public void mousePressed(MouseEvent e) {
        analyse.Analyse.printAnalysis("SocketIOListenerTableRowMenuListener","SocketIOListenerTableRowMenuListener");
        if (e.isPopupTrigger())
            doPop(e);

    }

    public void mouseReleased(MouseEvent e) {
        analyse.Analyse.printAnalysis("SocketIOListenerTableRowMenuListener","SocketIOListenerTableRowMenuListener");
        if (e.isPopupTrigger())
            doPop(e);

    }

    private void doPop(MouseEvent e) {
        analyse.Analyse.printAnalysis("SocketIOListenerMouseTableRowMenu","SocketIOListenerTableRowMenuListener");
        SocketIOListenerMouseTableRowMenu menu = new SocketIOListenerMouseTableRowMenu(panel);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}