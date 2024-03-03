package com.example.demo.repository.RepoPage;

public class Pageable {
    private int nr_page;
    private int size_page;

    public Pageable(int nr_page, int size_page) {
        this.nr_page = nr_page;
        this.size_page = size_page;
    }

    public int getNr_page() {
        return nr_page;
    }

    public int getSize_page() {
        return size_page;
    }
}
