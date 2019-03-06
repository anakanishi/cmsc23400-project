import android.hardware.SensorEvent;

import java.util.*;

public class ModelPredict {
    int seqL = 10;
    float[] AccxAxisUncalib = new ArrayList();
    float[] AccyAxisUncalib = new ArrayList();
    float[] AcczAxisUncalib = new ArrayList();

    float[] AccxAxisBias = new ArrayList();
    float[] AccyAxisBias = new ArrayList();
    float[] AcczAxisBias = new ArrayList();

    float[] gyroxAxis = new ArrayList();
    float[] gyroyAxis = new ArrayList();
    float[] gyrozAxis = new ArrayList();

    float[] linxAxis = new ArrayList();
    float[] linyAxis = new ArrayList();
    float[] linzAxis = new ArrayList();

    List<float> coef_D = [-0.7283668829747474,-0.7768152833091526,-0.9061645322089593,0.0,0.0,0.0,-0.5323914473146917,-0.4956679583505869,-0.2577685556299623,-0.8640316158242574,-0.787152838312696,-0.9976175405005523,0.15524122451148442,-0.12589679017653735,1.0494179513707045,0.0,0.0,0.0,0.028282870320711716,-0.10688854168267507,-0.01101024367103155,-0.10015605082596352,0.0630407901584411,-0.11625677266682886];
    List<float> coef_J = [2.7698288980300103,0.23126498804338338,1.3430111206930866,0.0,0.0,0.0,-7.09668077274666,-2.419849958454916,-0.042995929908431874,-3.5590924474936187,1.935800260385821,-2.0110434363294334,0.38052987467832056,-0.31022971481166445,-0.4803508770475169,0.0,0.0,0.0,0.032056562029748464,-0.9542371194500321,0.6718132454724997,0.6845756658521083,1.410952240356186,-0.06832937434406522];
    List<float> coef_R = [-2.5709337747082928,-0.3671956176584037,-5.838773990071596,0.0,0.0,0.0,5.216319380308161,-3.45574984610926,4.259438709701523,2.871880600576,0.5229752451395808,2.998856479092019,0.5228022641510728,0.4321515917348509,-0.04970757602510051,0.0,0.0,0.0,2.0756342878849057,0.86532071410412,-1.088470721346598,-0.5967283697703267,-0.2584465946132095,1.3433121893666156];
    List<float> coef_S = [0.21652378821315701,0.4466576380825852,4.272436405074599,0.0,0.0,0.0,1.2456399895955057,4.834322905880952,-0.2634084446500268,0.4873859482108931,-0.554638627488678,-1.518578660388239,-0.3985371366124926,0.2232445112335916,-0.502123729034665,0.0,0.0,0.0,-1.59133308476319,0.2607464428203567,0.7910611153765806,0.07340545574859109,-0.32660799406382673,-0.6529833947645031];
    List<float> coef_W = [0.6421418644552529,-0.07060217063395187,1.1659586037900493,0.0,0.0,0.0,1.0235000220046913,2.921572907033654,-2.6634492513078967,-0.15886030557628117,-3.3733247782870315,-1.701746605869355,-0.13520714398204148,-0.2901247498436159,-1.6511679519067102,0.0,0.0,0.0,-1.693142770573553,-0.4972579748832467,-1.357754245669117,-0.389048828486587,1.0978314268239935,-0.8861963786004996];
    float bias_D = -0.45362006;
    float bias_J = -1.10867751;
    float bias_R = -5.60522811;
    float bias_S = -13.02266532;
    float bias_W = 9.86654664;

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

    private double mean(ArrayList<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private double std(ArrayList<Integer> table) {
        double mean = mean(table);
        double temp = 0;
        for (int i = 0; i < table.size(); i++) {
            int val = table.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = (double) temp / (double) (table.size());
        return Math.sqrt(meanOfDiffs);
    }

    public void recordEvent(SensorEvent event) {

        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                float currentValue = event.values[0];
                mTextSensorLight.setText(getResources().getString(R.string.label_light, currentValue));
                break;
            case Sensor.TYPE_GYROSCOPE:
                float xAxisGyro = event.values[0];
                float yAxisGyro = event.values[1];
                float zAxisGyro = event.values[2];

                gyroxAxis.add(xAxisGyro);
                gyroyAxis.add(yAxisGyro);
                gyrozAxis.add(zAxisGyro);

                break;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                float xAxisUncalibAccel = event.values[0];
                float yAxisUncalibAccel = event.values[1];
                float zAxisUncalibAccel = event.values[2];
                float xAccelBias = event.values[3];
                float yAccelBias = event.values[4];
                float zAccelBias = event.values[5];

                AccxAxisUncalib.add(xAxisUncalibAccel);
                AccyAxisUncalib.add(yAxisUncalibAccel);
                AcczAxisUncalib.add(zAccelBias);

                AccxAxisBias.add(xAccelBias);
                AccyAxisBias.add(yAccelBias);
                AcczAxisBias.add(zAccelBias);

                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                float xAxisLinAccel = event.values[0];
                float yAxisLinAccel = event.values[1];
                float zAxisLinAccel = event.values[2];

                linxAxis.add(xAxisLinAccel);
                linyAxis.add(yAxisLinAccel);
                linzAxis.add(zAxisLinAccel);

                break;
            default:
                // do nothing
        }
        popAll();
    }

    private float predict(float[] x, List<float> coef, float bias) {
        float res = 0;
        int i = 0;

        for (i = 0; i < x.size(); i++) {
            res = res + x[i] * coef[i];
        }
        return res + bias;
    }

    public string modelPredict(SensorEvent event) {
        recordEvent(event);
        float mean_1 = mean(AccxAxisUncalib);
        float mean_2 = mean(AccyAxisUncalib);
        float mean_3 = mean(AcczAxisUncalib);
        float mean_4 = mean(AccxAxisBias);
        float mean_5 = mean(AccyAxisBias);
        float mean_6 = mean(AcczAxisBias);
        float mean_7 = mean(gyroxAxis);
        float mean_8 = mean(gyroyAxis);
        float mean_9 = mean(gyrozAxis);
        float mean_10 = mean(linxAxis);
        float mean_11 = mean(linyAxis);
        float mean_12 = mean(linzAxis);

        float std_1 = std(AccxAxisUncalib);
        float std_2 = std(AccyAxisUncalib);
        float std_3 = std(AcczAxisUncalib);
        float std_4 = std(AccxAxisBias);
        float std_5 = std(AccyAxisBias);
        float std_6 = std(AcczAxisBias);
        float std_7 = std(gyroxAxis);
        float std_8 = std(gyroyAxis);
        float std_9 = std(gyrozAxis);
        float std_10 = std(linxAxis);
        float std_11 = std(linyAxis);
        float std_12 = std(linzAxis);

        List<float> x = [
        mean_1, mean_2, mean_3, mean_4, mean_5, mean_6, mean_7, mean_8, mean_9, mean_10, mean_11, mean_12, std_1, std_2, std_3, std_4, std_5, std_6, std_7, std_8, std_9, std_10, std_11, std_12]
        ;

        probD = predict(x, coef_D, bias_D);
        probJ = predict(x, coef_J, bias_J);
        probR = predict(x, coef_R, bias_R);
        probS = predict(x, coef_S, bias_S);
        probW = predict(x, coef_W, bias_W);

        List<float> res = [probD, probJ, probR, probS, probW];
        float max = -100;
        int idx;
        for (int i = 0; i < res.size(); i++) {
            if res[i] > max {
                max = res[i];
                idx = i;
            }
        }

        if (idx == 0) {
            return "D"
        } else if (idx == 1) {
            return "J"
        } else if (idx == 2) {
            return "R"
        } else if (idx == 3) {
            return "S"
        } else {
            return "W"
        }

    }


}