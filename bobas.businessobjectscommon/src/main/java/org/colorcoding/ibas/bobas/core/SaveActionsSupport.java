package org.colorcoding.ibas.bobas.core;

public class SaveActionsSupport {

    public SaveActionsSupport(Object source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.source = source;
    }

    private static final int array_extension_step = 2;
    private Object source = null;
    private SaveActionsListener[] listeners;

    public void addListener(SaveActionsListener listener) {
        if (listener == null) {
            return;
        }
        if (this.listeners == null) {
            this.listeners = new SaveActionsListener[array_extension_step];
        }
        boolean done = false;
        // 检查是否已监听
        for (int i = 0; i < this.listeners.length; i++) {
            if (this.listeners[i] == listener) {
                done = true;
                break;
            }
        }
        if (!done) {
            // 没有监听
            done = false;
            for (int i = 0; i < this.listeners.length; i++) {
                if (this.listeners[i] == null) {
                    this.listeners[i] = listener;
                    done = true;
                    break;
                }
            }
        }
        if (!done) {
            // 数组不够
            SaveActionsListener[] tmps = new SaveActionsListener[this.listeners.length + array_extension_step];
            int i = 0;
            for (; i < this.listeners.length; i++) {
                tmps[i] = this.listeners[i];
            }
            tmps[i] = listener;
            this.listeners = tmps;
        }
    }

    public void removeListener(SaveActionsListener listener) {
        if (listener == null) {
            return;
        }
        if (this.listeners == null) {
            return;
        }
        for (int i = 0; i < this.listeners.length; i++) {
            if (this.listeners[i] == listener) {
                this.listeners[i] = null;
            }
        }
    }

    public boolean fireActions(SaveActionsType type, IBusinessObjectBase bo, IBusinessObjectBase root) {
        if (this.listeners == null) {
            return true;
        }
        for (SaveActionsListener item : this.listeners) {
            if (item == null) {
                continue;
            }
            boolean value = item.actionsEvent(new SaveActionsEvent(this.source, type, bo, root));
            if (!value) {
                // 返回为false，直接退出
                return value;
            }
        }
        return true;
    }

    public boolean fireActions(SaveActionsType type, IBusinessObjectBase bo) {
        return this.fireActions(type, bo, null);
    }
}
