package com.company;

public class Vertex extends Abstract{
    final private String id;
    private String name;

    // create a vertex with id and name
    public Vertex(String id, String name) {
        super(id);
        this.id = id;
        this.name = name;
    }

    @Override
    public String getID() {
        return super.getID();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Vertex other = (Vertex) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void printMe() {
        System.out.println("Vertex " + id);
    }
}
