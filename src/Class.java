import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class Class {
    public ArrayList<String> mus;
    private String name;
    private int length;
    private Vector[] objects;
    private Set<String> hashes;
    private double[] b;
    private BitSet f;

    public Class(String name, int n, Vector[] vectors) {
        this.name = name;
        length = n;
        objects = vectors;
        b = new double[n];
        f = new BitSet(n);
        hashes = new HashSet<String>(objects.length);
        for (Vector vector : objects)
            hashes.add(vector.serialize());
    }

    public boolean inClass(Vector v) {
        return hashes.contains(v.serialize());
    }

    public double calcPropertyProbability(int propertyIndex) {
        if (!f.get(propertyIndex)) {
            double sum = 0;
            for (Vector v : objects) {
                sum += v.get(propertyIndex);
            }
            b[propertyIndex] = objects.length > 0 ? sum / objects.length : 0;
            f.set(propertyIndex);
        }
        return b[propertyIndex];
    }

    public double calcMu(SetProperties props, int classno, Vector unknown) {
        double max = -1;
        for (Vector v : objects) {
            double mu = v.calcMu(props, classno, unknown);
            max = Math.max(max, mu);
        }
        return max;
    }

    public String getName() {
        return name;
    }

}
