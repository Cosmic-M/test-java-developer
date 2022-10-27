package service.impl;

public interface ParseService {
    void updateBids(int price, int size);

    void updateAsks(int price, int size);

    void removeFromBestAsks(int sizeToRemove);

    void removeFromBestBids(int sizeToRemove);

    void markBestAsk();

    void markBestBid();

    void markSizeByPrice(int price);
}
