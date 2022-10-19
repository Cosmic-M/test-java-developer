package service;

public interface ParseService {
    void parse(StringBuilder operations);

    StringBuilder getResult();

    void resetOrderBook();
}
