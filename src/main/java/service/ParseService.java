package service;

public interface ParseService {
    void findBestAsk();

    void findBestBid();

    void findSizeByPrice(int price);

    void updateBids(int price, int size);

    void updateAsks(int price, int size);

    void removeFromAsks(int sizeToRemove);

    void removeFromBids(int sizeToRemove);

    StringBuilder getResult();
}
