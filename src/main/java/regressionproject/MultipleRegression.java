package regressionproject;

import static java.lang.System.out;

import java.io.FileReader;
import java.text.NumberFormat;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.opencsv.CSVReader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class MultipleRegression extends Application {
    private final double[] data = new double[100];
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private final XYChart.Series predictedSeries = new XYChart.Series();
    private final XYChart.Series tarSeries = new XYChart.Series();
    private final XYChart.Series nicotineSeries = new XYChart.Series();
    private final XYChart.Series weightSeries = new XYChart.Series();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Scatter Chart Sample");
        final NumberAxis yAxis = new NumberAxis(0, 18, 0.5);
        final NumberAxis xAxis = new NumberAxis(0, 20, 0.5);
        final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        xAxis.setLabel("Contents");
        yAxis.setLabel("Carbon Monoxide");
        scatterChart.setTitle("Cigarette Scatter Graph");

        tarSeries.setName("Tar");
        nicotineSeries.setName("Nicotine");
        weightSeries.setName("Weight");

        int i = 0;
        try (CSVReader dataReader = new CSVReader(new FileReader("/Users/alket/eclipse-workspace/regressionproject/data/data.csv"), ',')) {
            String[] nextLine;
            while ((nextLine = dataReader.readNext()) != null) {
                String brandName = nextLine[0];
                double tarContent = Double.parseDouble(nextLine[1]);
                double nicotineContent = Double.parseDouble(nextLine[2]);
                double weight = Double.parseDouble(nextLine[3]);
                double carbonMonoxideContent = Double.parseDouble(nextLine[4]);
System.out.println(brandName+","+tarContent+","+nicotineContent+","+weight+","+carbonMonoxideContent);
                data[i++] = carbonMonoxideContent;
                data[i++] = tarContent;
                data[i++] = nicotineContent;
                data[i++] = weight;

                tarSeries.getData().add(new XYChart.Data(carbonMonoxideContent, tarContent));
                nicotineSeries.getData().add(new XYChart.Data(carbonMonoxideContent, nicotineContent));
                weightSeries.getData().add(new XYChart.Data(carbonMonoxideContent, weight));
            }
        }
        performRegression();

        scatterChart.getData().addAll(tarSeries, nicotineSeries, weightSeries, predictedSeries);
        Scene scene = new Scene(scatterChart, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void performRegression() {
        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        //double[] data = new double[75];// = {13.6, 14.1, 0.86, 0.9853, 16.6, 16, 1.06, 1.0938, 23.5, 29.8, 2.03, 1.165, 10.2, 8, 0.67, 0.928, 5.4, 4.1, 0.4, 0.9462, 15, 15, 1.04, 0.8885, 9, 8.8, 0.76, 1.0267, 12.3, 12.4, 0.95, 0.9225, 16.3, 16.6, 1.12, 0.9372, 15.4, 14.9, 1.02, 0.8858, 13, 13.7, 1.01, 0.9643, 14.4, 15.1, 0.9, 0.9316, 10, 7.8, 0.57, 0.9705, 10.2, 11.4, 0.78, 1.124, 9.5, 9, 0.74, 0.8517, 1.5, 1, 0.13, 0.7851, 18.5, 17, 1.26, 0.9186, 12.6, 12.8, 1.08, 1.0395, 17.5, 15.8, 0.96, 0.9573, 4.9, 4.5, 0.42, 0.9106, 15.9, 14.5, 1.01, 1.007, 8.5, 7.3, 0.61, 0.9806, 10.6, 8.6, 0.69, 0.9693, 13.9, 15.2, 1.02, 0.9496, 14.9, 12, 0.82, 1.1184}; // 1
        int numberOfObservations = 25;
        int numberOfIndependentVariables = 3;

        try {
            ols.newSampleData(data, numberOfObservations, numberOfIndependentVariables);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        numberFormat.setMaximumFractionDigits(2);
        double[] parameters = ols.estimateRegressionParameters();
        for (int i = 0; i < parameters.length; i++) {
            out.println("Parameter " + i +": " + numberFormat.format(parameters[i]));
        }

        predictedSeries.setName("Predicted");
        // SalemUltra,4.5,.42,.9106,4.9
        double arguments1[] = {1, 4.5, 0.42, 0.9106};
        predictedSeries.getData().add(
                new XYChart.Data(4.9, getY(parameters,arguments1)));
        out.println("X: " + 4.9 + "  y: " + 
                numberFormat.format(getY(parameters,arguments1)));
        
        // VirginiaSlims,15.2,1.02,.9496,13.9
        double arguments2[] = {1, 15.2, 1.02, 0.9496};
        predictedSeries.getData().add(
                new XYChart.Data(13.9, getY(parameters,arguments2)));
        out.println("X: " + 13.9 + "  y: " + 
                numberFormat.format(getY(parameters,arguments2)));
        
        double arguments3[] = {1, 12.2, 1.65, 0.86};
        predictedSeries.getData().add(
                new XYChart.Data(9.9, getY(parameters,arguments3)));
        out.println("X: " + 9.9 + "  y: " + 
                numberFormat.format(getY(parameters,arguments3)));
    }

    public double getY(double[] parameters, double[] arguments) {
        double result = 0;
        for(int i=0; i<parameters.length; i++) {
            result += parameters[i] * arguments[i];
        }
        return result;
    }

    public void displayAttribute(String attribute, double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        out.println(attribute + ": " + numberFormat.format(value));
    }

    public static void main(String[] args) {
        launch(args);
    }

}

