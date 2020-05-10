import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

final class PolynomialLeader extends PlayerImpl {

    private final static int HISTORY_RECORDS = 99;
    private final static int WINDOW_SIZE = 20;
    private int currentDayNumber;
    private static List<Record> recordDataList;
    private float coefficientA;
    private float coefficientB;
    private float coefficientC;

    PolynomialLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "Polynomial Leader");
    }

    public static void main(final String[] p_args)
            throws RemoteException, NotBoundException {
        new PolynomialLeader();
    }

    @Override
    public void checkConnection() throws RemoteException {
        super.checkConnection();
        //TO DO: delete the line above and put your own code here
    }

    @Override
    public void goodbye() throws RemoteException {
        super.goodbye();
        //TO DO: delete the line above and put your own exit code here
    }

    @Override
    public void startSimulation(int p_steps) throws RemoteException {
        recordDataList = new ArrayList<>();
        currentDayNumber = HISTORY_RECORDS;
        initialiseHistoryData();
    }

    private void initialiseHistoryData() throws RemoteException {
        for (int day = 1; day <= HISTORY_RECORDS; day++) {
            recordDataList.add(m_platformStub.query(PlayerType.LEADER, day));
        }
    }

    // This method will approximate follower's reaction to leader's price
    private void calculatePolynomialFunction() {
        float leaderPrice;
        float followerPrice;
        double[] predictor = new double[WINDOW_SIZE];
        double[] response = new double[WINDOW_SIZE];

        int counter = 0;
        for (int day = 0; day < recordDataList.size(); day++) {
            if (day >= (recordDataList.size() - WINDOW_SIZE)) {
                Record record = recordDataList.get(day);
                leaderPrice = record.m_leaderPrice;
                predictor[counter] = leaderPrice;
                followerPrice = record.m_followerPrice;
                response[counter++] = followerPrice;
            }
        }

        PolynomialRegression regression = new PolynomialRegression(predictor, response, 2);
        coefficientA = (float) regression.beta(0);
        coefficientB = (float) regression.beta(1);
        coefficientC = (float) regression.beta(2);
    }

    private float calculateLeaderPrice() {
        System.out.println(String.format("\nCoefficient A: %f\nCoefficient B: %f\nCoefficient C: %f", coefficientA, coefficientB, coefficientC));
        if (coefficientC == 0) {
            return (3 * (coefficientA - coefficientB + 10)) / (20 - 6 * coefficientB);
        } else {
            double intermediate = -3 * (9 * coefficientA + 70) * coefficientC + 9 * coefficientB * coefficientB + coefficientB * (9 * coefficientC - 60) + 9 * coefficientC * coefficientC + 100;
            double square_root;
            if (intermediate < 0) {
                square_root = Math.sqrt(-1 * intermediate);
                square_root *= -1;
            } else
                square_root = Math.sqrt(intermediate);

            System.out.println("Square Root: " + square_root);
            double numerator = (square_root + 3 * coefficientB - 3 * coefficientC - 10);
            System.out.println("Numerator: " + numerator);
            double denominator = 9 * coefficientC;
            System.out.println("Denominator: " + denominator);

            double price = (-1 * numerator / denominator);
            System.out.println("Current price: " + price);

            return (float) price;
        }
    }

    @Override
    public void endSimulation() throws RemoteException {
        System.out.println("Finished Simulation.");
    }

    @Override
    public void proceedNewDay(int p_date) throws RemoteException {
        currentDayNumber++;
        recordDataList.add(m_platformStub.query(PlayerType.LEADER, currentDayNumber));
        calculatePolynomialFunction();
        float leaderPublishedPrice = calculateLeaderPrice();
        m_platformStub.publishPrice(PlayerType.LEADER, leaderPublishedPrice);
    }
}
