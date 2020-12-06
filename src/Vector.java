import java.util.BitSet;

public class Vector {
    private String name;
    private BitSet vector;

    public Vector(String name, int length) {
        this.name = name;
        vector = new BitSet(length);
    }

    public Vector(String name, BitSet properties) {
        this.name = name;
        vector = new BitSet(properties.length());
        vector.xor(properties);
    }

    public int get(int index) {
        return vector.get(index) ? 1 : 0;
    }

    public double calcMu(SetProperties props, int classno, Vector unknown) {
        double s = props.getS(classno);
        double c = 0;
        for (int i = 0, sz = vector.length(); i < sz; i++) {
            c += (unknown.get(i) == get(i) ? 1 : -1) * props.getA(i, classno);
        }
        c = s != 0 ? c / s : 0;
        return Math.max(0, c);
    }

    public String getName() {
        return name;
    }

    public BitSet getBitSet() {
        return vector;
    }

    public String serialize() {
        StringBuilder strB = new StringBuilder();
        for (int i = 0; i < vector.length(); i++)
            strB.append(vector.get(i) ? 1 : 0);
        return strB.toString();
    }
}
