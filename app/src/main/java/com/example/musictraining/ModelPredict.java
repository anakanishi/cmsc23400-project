package com.example.musictraining;
import android.hardware.*;

import java.util.*;

public class ModelPredict{
    private int seqL = 10;
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
    private List<Double>  coef_R;
    private List<Double>  coef_S;
    private List<Double>  coef_W;
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

        coef_D = Arrays.asList(-0.7283668829747474,-0.7768152833091526,-0.9061645322089593,0.0,0.0,0.0,-0.5323914473146917,-0.4956679583505869,-0.2577685556299623,-0.8640316158242574,-0.787152838312696,-0.9976175405005523,0.15524122451148442,-0.12589679017653735,1.0494179513707045,0.0,0.0,0.0,0.028282870320711716,-0.10688854168267507,-0.01101024367103155,-0.10015605082596352,0.0630407901584411,-0.11625677266682886);
        coef_J = Arrays.asList(2.7698288980300103,0.23126498804338338,1.3430111206930866,0.0,0.0,0.0,-7.09668077274666,-2.419849958454916,-0.042995929908431874,-3.5590924474936187,1.935800260385821,-2.0110434363294334,0.38052987467832056,-0.31022971481166445,-0.4803508770475169,0.0,0.0,0.0,0.032056562029748464,-0.9542371194500321,0.6718132454724997,0.6845756658521083,1.410952240356186,-0.06832937434406522);
        coef_R = Arrays.asList(-2.5709337747082928,-0.3671956176584037,-5.838773990071596,0.0,0.0,0.0,5.216319380308161,-3.45574984610926,4.259438709701523,2.871880600576,0.5229752451395808,2.998856479092019,0.5228022641510728,0.4321515917348509,-0.04970757602510051,0.0,0.0,0.0,2.0756342878849057,0.86532071410412,-1.088470721346598,-0.5967283697703267,-0.2584465946132095,1.3433121893666156);
        coef_S = Arrays.asList(0.21652378821315701,0.4466576380825852,4.272436405074599,0.0,0.0,0.0,1.2456399895955057,4.834322905880952,-0.2634084446500268,0.4873859482108931,-0.554638627488678,-1.518578660388239,-0.3985371366124926,0.2232445112335916,-0.502123729034665,0.0,0.0,0.0,-1.59133308476319,0.2607464428203567,0.7910611153765806,0.07340545574859109,-0.32660799406382673,-0.6529833947645031);
        coef_W = Arrays.asList(0.6421418644552529,-0.07060217063395187,1.1659586037900493,0.0,0.0,0.0,1.0235000220046913,2.921572907033654,-2.6634492513078967,-0.15886030557628117,-3.3733247782870315,-1.701746605869355,-0.13520714398204148,-0.2901247498436159,-1.6511679519067102,0.0,0.0,0.0,-1.693142770573553,-0.4972579748832467,-1.357754245669117,-0.389048828486587,1.0978314268239935,-0.8861963786004996);
        bias_D = -0.45362006;
        bias_J = -1.10867751;
        bias_R = -5.60522811;
        bias_S = -13.02266532;
        bias_W = 9.86654664;
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

        double[] x = {
        mean_1, mean_2, mean_3, mean_4, mean_5, mean_6, mean_7, mean_8, mean_9, mean_10, mean_11, mean_12, std_1, std_2, std_3, std_4, std_5, std_6, std_7, std_8, std_9, std_10, std_11, std_12}
        ;

        double probD = predict(x, coef_D, bias_D);
        double probJ = predict(x, coef_J, bias_J);
        double probR = predict(x, coef_R, bias_R);
        double probS = predict(x, coef_S, bias_S);
        double probW = predict(x, coef_W, bias_W);

        double[] res = {probD, probJ, probR, probS, probW};
        double max = -100;
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
        } else if (idx == 3) {
            return "S";
        } else {
            return "W";
        }

    }


}