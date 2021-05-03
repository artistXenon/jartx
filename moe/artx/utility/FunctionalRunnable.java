package moe.artx.utility;

public abstract class FunctionalRunnable<T> implements Runnable {

    volatile private T returnValue;
    private Exception thrownException;

    @Override
    public void run() {
        try {
            setReturnValue(onTry());
        }
        catch (Exception e) {
            throwException(e);
        }
    }

    protected void throwException(Exception e) {
        this.thrownException = e;
        onCatch(this.thrownException);
    }

    protected void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }

    public T getReturnValue() {
        return this.returnValue;
    }

    protected abstract T onTry() throws Exception;
    protected abstract void onCatch(Exception e);
    protected abstract void onFinal(T returnValue);
}
