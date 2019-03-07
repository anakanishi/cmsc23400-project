package com.example.musictraining;

import android.hardware.*;

import java.util.*;

public class ModelPredict {
    private int seqL = 15;
    private ArrayList<Double> AccxAxisUncalib;
    private ArrayList<Double> AccyAxisUncalib;
    private ArrayList<Double> AcczAxisUncalib;

    private ArrayList<Double> AccxAxisBias;
    private ArrayList<Double> AccyAxisBias;
    private ArrayList<Double> AcczAxisBias;

    private ArrayList<Double> gyroxAxis;
    private ArrayList<Double> gyroyAxis;
    private ArrayList<Double> gyrozAxis;

    private ArrayList<Double> linxAxis;
    private ArrayList<Double> linyAxis;
    private ArrayList<Double> linzAxis;

    private List<Double> coef_D;
    private List<Double> coef_J;
    private List<Double> coef_R;
    private List<Double> coef_S;
    private List<Double> coef_W;
    private double bias_D;
    private double bias_J;
    private double bias_R;
    private double bias_S;
    private double bias_W;

    public ModelPredict() {
        AccxAxisUncalib = new ArrayList<Double>();
        AccyAxisUncalib = new ArrayList<Double>();
        AcczAxisUncalib = new ArrayList<Double>();

        AccxAxisBias = new ArrayList<Double>();
        AccyAxisBias = new ArrayList<Double>();
        AcczAxisBias = new ArrayList<Double>();

        gyroxAxis = new ArrayList<Double>();
        gyroyAxis = new ArrayList<Double>();
        gyrozAxis = new ArrayList<Double>();

        linxAxis = new ArrayList<Double>();
        linyAxis = new ArrayList<Double>();
        linzAxis = new ArrayList<Double>();

        coef_D = Arrays.asList(
                -0.7071706391194564, -0.7462463150147243, -0.9203057590870861, 0.001095649426648337, 0.0007570769966484753, 0.0008819053521848226, -0.535547018550611, -0.49979755532590187, -0.2300472678543208, -0.8509206096094255, -0.7755612754709383, -1.022673676849119, 0.15376474867187184, -0.1212000528086833, 1.0640077676531285, 8.259287044820296e-06, 7.244406239681733e-06, 7.273598084611823e-05, 0.02895206250897197, -0.11400294614842736, -0.012956176498972145, -0.0917088695885575, 0.06567287750481504, -0.11271009179370213
        );
        coef_J = Arrays.asList(
                1.3262752313532553, 0.3632880742850401, 2.59169355689225, -0.08399677962689404, -0.06032570941191145, -0.0714151423477406, -5.217597382508843, -2.1112026950706526, -0.5537785459486929, -1.8458611560647449, 1.8793017765465458, -2.005900581911727, 0.014556620974273857, -0.32594634807021927, -0.46004750658243987, -0.0003329978354045565, 0.003979919953501252, 0.001199308206780176, 0.025148591732594483, -0.6802297741079085, 0.37810538870398036, 1.7391928126812561, 1.4420594448803328, -2.0891731260963966
        );
        coef_R = Arrays.asList(
                -2.5244779350665403, 0.031569042434290547, -5.488003828939462, 1.7691764028531975, 1.2315097290210473, 1.397429386083336, 6.794013538914299, -0.12752700279443338, 4.926932860755729, 2.901070333266968, 0.2273434509474713, 2.905781918073421, 0.40940227548368363, 0.5790804600479343, 0.1660924759145838, 0.03692963965181742, -0.08425107114142256, 0.10492230255666932, 1.380815568906142, -0.7022684789134603, -0.5564678167866368, 0.05720629409221694, -1.0877942166864685, 2.14891228154359
        );
        coef_W = Arrays.asList(
                -0.12352744800972643, -0.2881036240332982, 2.4195377705694048, -1.606855572301363, -1.3324465862356225, -0.975524913853857, -1.2157831113846074, 1.3028265415169737, -4.143377839667304, 0.3377386557238442, -2.7013391410972383, -0.4393518667055321, -0.09360787136721149, -0.2918742808877694, -1.6858508654067412, -0.006652582293916143, 0.02799731665544216, -0.23996871305254364, -0.04625445008293719, 1.339403860792689, -1.0709134794016104, -1.1633526617062355, 1.0761008238992118, -1.6369368573786673
        );
        //        coef_S = Arrays.asList(0.21652378821315701, 0.4466576380825852, 4.272436405074599, 0.0, 0.0, 0.0, 1.2456399895955057, 4.834322905880952, -0.2634084446500268, 0.4873859482108931, -0.554638627488678, -1.518578660388239, -0.3985371366124926, 0.2232445112335916, -0.502123729034665, 0.0, 0.0, 0.0, -1.59133308476319, 0.2607464428203567, 0.7910611153765806, 0.07340545574859109, -0.32660799406382673, -0.6529833947645031);

        bias_D = -0.45607265;
        bias_J = -3.84957677;
        bias_R = -11.43076811;
        bias_W = 9.6520626;
        //        bias_S = -13.02266532;

    }

    private void popAll() {
        if (AccxAxisUncalib.size() > seqL) {
            AccxAxisUncalib.remove(0);
        }
        if (AccyAxisUncalib.size() > seqL) {
            AccyAxisUncalib.remove(0);
        }
        if (AcczAxisUncalib.size() > seqL) {
            AcczAxisUncalib.remove(0);
        }
        if (AccxAxisBias.size() > seqL) {
            AccxAxisBias.remove(0);
        }
        if (AccyAxisBias.size() > seqL) {
            AccyAxisBias.remove(0);
        }
        if (AcczAxisBias.size() > seqL) {
            AcczAxisBias.remove(0);
        }
        if (gyroxAxis.size() > seqL) {
            gyroxAxis.remove(0);
        }
        if (gyroyAxis.size() > seqL) {
            gyroyAxis.remove(0);
        }
        if (gyrozAxis.size() > seqL) {
            gyrozAxis.remove(0);
        }
        if (linxAxis.size() > seqL) {
            linxAxis.remove(0);
        }
        if (linyAxis.size() > seqL) {
            linyAxis.remove(0);
        }
        if (linzAxis.size() > seqL) {
            linzAxis.remove(0);
        }
        return;
    }

    private double mean(ArrayList<Double> marks) {
        double sum = 0.0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    private double std(ArrayList<Double> table) {
        double mean = mean(table);
        double temp = 0.0;
        for (int i = 0; i < table.size(); i++) {
            double val = table.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = (double) temp / (double) (table.size());
        return Math.sqrt(meanOfDiffs);
    }

    public void recordEvent(SensorEvent event) {

        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                double xAxisGyro = event.values[0];
                double yAxisGyro = event.values[1];
                double zAxisGyro = event.values[2];

                gyroxAxis.add(xAxisGyro);
                gyroyAxis.add(yAxisGyro);
                gyrozAxis.add(zAxisGyro);

                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                double xAxisUncalibAccel = event.values[0];
                double yAxisUncalibAccel = event.values[1];
                double zAxisUncalibAccel = event.values[2];
                double xAccelBias = event.values[3];
                double yAccelBias = event.values[4];
                double zAccelBias = event.values[5];

                AccxAxisUncalib.add(xAxisUncalibAccel);
                AccyAxisUncalib.add(yAxisUncalibAccel);
                AcczAxisUncalib.add(zAccelBias);

                AccxAxisBias.add(xAccelBias);
                AccyAxisBias.add(yAccelBias);
                AcczAxisBias.add(zAccelBias);

                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                double xAxisLinAccel = event.values[0];
                double yAxisLinAccel = event.values[1];
                double zAxisLinAccel = event.values[2];

                linxAxis.add(xAxisLinAccel);
                linyAxis.add(yAxisLinAccel);
                linzAxis.add(zAxisLinAccel);

                break;
            default:
                // do nothing
        }
        popAll();
    }

    private double predict(double[] x, List<Double> coef, double bias) {
        double res = 0;

        for (int i = 0; i < x.length; i++) {
            res = res + x[i] * coef.get(i);
        }
        return res + bias;
    }

    public String modelPredict(SensorEvent event) {
        recordEvent(event);
        double mean_1 = mean(AccxAxisUncalib);
        double mean_2 = mean(AccyAxisUncalib);
        double mean_3 = mean(AcczAxisUncalib);
        double mean_4 = mean(AccxAxisBias);
        double mean_5 = mean(AccyAxisBias);
        double mean_6 = mean(AcczAxisBias);
        double mean_7 = mean(gyroxAxis);
        double mean_8 = mean(gyroyAxis);
        double mean_9 = mean(gyrozAxis);
        double mean_10 = mean(linxAxis);
        double mean_11 = mean(linyAxis);
        double mean_12 = mean(linzAxis);

        double std_1 = std(AccxAxisUncalib);
        double std_2 = std(AccyAxisUncalib);
        double std_3 = std(AcczAxisUncalib);
        double std_4 = std(AccxAxisBias);
        double std_5 = std(AccyAxisBias);
        double std_6 = std(AcczAxisBias);
        double std_7 = std(gyroxAxis);
        double std_8 = std(gyroyAxis);
        double std_9 = std(gyrozAxis);
        double std_10 = std(linxAxis);
        double std_11 = std(linyAxis);
        double std_12 = std(linzAxis);

        double[] x = {std_1, std_2, std_3, std_4, std_5, std_6, std_7, std_8, std_9, std_10, std_11, std_12, mean_1, mean_2, mean_3, mean_4, mean_5, mean_6, mean_7, mean_8, mean_9, mean_10, mean_11, mean_12};

        double probD = predict(x, coef_D, bias_D);
        double probJ = predict(x, coef_J, bias_J);
        double probR = predict(x, coef_R, bias_R);
        double probS = predict(x, coef_S, bias_S);
        double probW = predict(x, coef_W, bias_W);

        double[] res = {probD, probJ, probR, probS, probW};
        double max = -1000;
        int idx = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i] > max) {
                max = res[i];
                idx = i;
            }
        }

        if (idx == 0) {
            return "D";
        } else if (idx == 1) {
            return "J";
        } else if (idx == 2) {
            return "R";
        } else {
            return "W";
        }

    }


}