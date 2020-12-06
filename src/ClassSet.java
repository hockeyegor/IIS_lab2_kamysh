import java.util.ArrayList;

class ClassesSet {

    private Class[] classes;
    private String name;
    private int length;
    private SetProperties properties;

    public ClassesSet(String name, Class[] classes, int n) {
        this.classes = classes;
        this.name = name;
        length = n;
    }

    public SetProperties calcTaskProperties() {
        if (properties != null) {
            return properties;
        }

        double b[] = new double[length];
        double a[][] = new double[length][classes.length];
        double tmpb[] = new double[classes.length];
        int csize = classes.length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < csize; j++) {
                tmpb[j] = classes[j].calcPropertyProbability(i);
                b[i] += tmpb[j];
            }
            b[i] /= csize;
            for (int j = 0; j < csize; j++) {
                a[i][j] = Math.abs(tmpb[j] - b[i]);
            }
        }

        properties = new SetProperties(a);
        return properties;
    }

    Class checkIfKnown(Vector v) {
        for (Class c : classes) {
            if (c.inClass(v)) {
                return c;
            }
        }
        return null;
    }

    public Class belongs(Vector unknown) {
        ArrayList<String> mus = new ArrayList();
        if (properties == null) {
            calcTaskProperties();
        }
        Class pKnown = checkIfKnown(unknown);
        if (pKnown != null) {
            return pKnown;
        }

        double max = -1;
        int classindex = -1;
        for (int i = 0; i < classes.length; i++) {
            double mu = classes[i].calcMu(properties, i, unknown);
            mus.add(classes[i].getName() + " - " + mu);
            if (mu > max) {
                max = mu;
                classindex = i;
            }
        }
        if (classindex > -1) {
            classes[classindex].mus = mus;
        }
        return classindex > -1 ? classes[classindex] : null;
    }
}
