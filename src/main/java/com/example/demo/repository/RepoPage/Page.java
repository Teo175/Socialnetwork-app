package com.example.demo.repository.RepoPage;


public class Page <E> {
    private Iterable<E> elem;
    private int nr_elems;

    public Iterable<E> getElem() {
        return elem;
    }

    public int getNr_elems() {
        return nr_elems;
    }

    public Page(Iterable<E> elem, int nr_elems) {
        this.elem = elem;
        this.nr_elems = nr_elems;
    }
}
