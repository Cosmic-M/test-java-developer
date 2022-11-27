package concurrent;

import service.impl.ParseServiceImpl;

public class DataBinder {
    private boolean transfer;
    private int operation;
    private int price;
    private int size;

    public synchronized void renewDataNotify(int operation, int price, int size) {
        while (transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        "interrupted during waiting inside renewDataNotify method: " + e);
            }
        }
        transfer = true;
        this.operation = operation;
        this.price = price;
        this.size = size;
        notify();
    }

    public synchronized int getOperationNumber() {
        while (!transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        "interrupted during waiting inside processTrigger method: " + e);
            }
        }
        transfer = false;
        notify();
        ParseServiceImpl.prise = price ;
        ParseServiceImpl.size = size;
        return operation;
    }
}
