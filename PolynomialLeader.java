import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

final class PolynomialLeader extends PlayerImpl {

    private final static int HISTORY_RECORDS = 99;
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
    private void calculateLinearFunction() {
        float sumLeaderSquares = 0;
        float sumLeader = 0;
        float sumFollower = 0;
        float sumLeaderFollowerProduct = 0;
        float leaderPrice;
        float followerPrice;
        double[] predictor = new double[recordDataList.size()];
        double[] response = new double[recordDataList.size()];

        int counter = 0;
        for (Record record : recordDataList) {
            leaderPrice = record.m_leaderPrice;
            predictor[counter] = leaderPrice;
            followerPrice = record.m_followerPrice;
            response[counter++] = followerPrice;
            sumLeaderSquares += leaderPrice * leaderPrice;
            sumLeader += leaderPrice;
            sumFollower += followerPrice;
            sumLeaderFollowerProduct += leaderPrice * followerPrice;
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
            double square_root = Math.sqrt(-3 * (9 * coefficientA + 70) * coefficientC + 9 * coefficientB * coefficientB + coefficientB * (9 * coefficientC - 60) + 9 * coefficientC * coefficientC + 100);
            System.out.println("Radical: " + square_root);
            double numerator = (square_root + 3 * coefficientB - 3 * coefficientC - 10);
            System.out.println("Numarator: " + numerator);
            double denominator = 9 * coefficientC;
            System.out.println("Numitor: " + denominator);

            double price = (-1 * numerator / denominator);
            System.out.println("Current price: " + price);

            return (float) price;
        }
    }

    @Override
    public void endSimulation() throws RemoteException {
        System.out.println("Am invins");
    }

    @Override
    public void proceedNewDay(int p_date) throws RemoteException {
        currentDayNumber++;
        recordDataList.add(m_platformStub.query(PlayerType.LEADER, currentDayNumber));
        calculateLinearFunction();
        float leaderPublishedPrice = calculateLeaderPrice();
        m_platformStub.publishPrice(PlayerType.LEADER, leaderPublishedPrice);
    }
}
