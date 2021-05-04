package moe.artx.utility;

import java.lang.ref.WeakReference;

public abstract class AttendedData<T> {

    private T value;
    private WeakReference<Attender<T>> attender;

    public synchronized void setValue(T value){
        changeValue(value);
    }

    private void changeValue(T value) {
        if (attender.get() != null)
            attender.get().onValueChange(this.value, value);
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void pushAttender(Attender<T> attender) {
        if (attender == null) throw new IllegalArgumentException();
        this.attender = new WeakReference<>(attender);
    }

    public interface Attender<T> {
        void onValueChange(T oldValue, T newValue);
    }
}
