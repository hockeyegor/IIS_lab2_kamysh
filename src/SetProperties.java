public class SetProperties {
    private double a[][];
    private double s[];

    public SetProperties(double a[][]) {
        this.a = a;
        calcS();
    }

    private void calcS() {
        if (a.length > 0) {
            s = new double[a[0].length];
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < a.length; j++) {
                    s[i] += a[j][i];
                }
            }
        } else {
            s = new double[0];
        }
    }

    public double getS(int classIndex) {
        return s[classIndex];
    }

    public double getA(int property, int classno) {
        return a[property][classno];
    }
}
