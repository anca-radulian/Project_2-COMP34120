import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

final class CaesarLeader extends PlayerImpl {

    private final static int HISTORY_RECORDS = 99;
    private int currentDayNumber;
    private static List<Record> recordDataList;
    private float coefficientA;
    private float coefficientB;
    private float leaderPublishedPrice;

    CaesarLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "Caesar Leader");
    }

    public static void main(final String[] p_args)
            throws RemoteException, NotBoundException
    {
        new CaesarLeader();
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

    private void updateLeaderPrice() throws RemoteException {
        currentDayNumber++;
        recordDataList.add(m_platformStub.query(PlayerType.LEADER, currentDayNumber));
        calculateLinearFunction();
        leaderPublishedPrice = calculateLeaderPrice();
    }

    // This method will approximate follower's reaction to leader's price
    private void calculateLinearFunction() {
        float sumLeaderSquares = 0;
        float sumLeader = 0;
        float sumFollower = 0;
        float sumLeaderFollowerProduct = 0;

        for (Record record : recordDataList) {
            float leaderPrice = record.m_leaderPrice;
            float followerPrice = record.m_followerPrice;
            sumLeaderSquares += leaderPrice*leaderPrice;
            sumLeader += leaderPrice;
            sumFollower += followerPrice;
            sumLeaderFollowerProduct += leaderPrice*followerPrice;
        }

        coefficientA = (sumLeaderSquares*sumFollower - sumLeader*sumLeaderFollowerProduct)
                / (currentDayNumber*sumLeaderSquares - sumLeader*sumLeader);
        coefficientB = (currentDayNumber*sumLeaderFollowerProduct - sumLeader*sumFollower)
                / (currentDayNumber*sumLeaderSquares - sumLeader*sumLeader);

    }

    private float calculateLeaderPrice() {
        System.out.println(String.format("\nCoefficient A: %f\nCoefficient B: %f", coefficientA, coefficientB));
        return (float) (( -3 - 0.3*coefficientA + 0.3*coefficientB)
                / (2* ( 0.3*coefficientB - 1)));
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
        leaderPublishedPrice = calculateLeaderPrice();
        m_platformStub.publishPrice(PlayerType.LEADER, leaderPublishedPrice);
    }

}
