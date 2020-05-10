import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

final class ForgettingFactor extends PlayerImpl{

    private final static int HISTORY_RECORDS = 99;
    private final static int WINDOW_SIZE = 100;
    private int currentDayNumber;
    private static List<Record> recordDataList;
    private float coefficientA;
    private float coefficientB;

    ForgettingFactor() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "Forgetting Factor Leader");
    }

    public static void main(final String[] p_args)
            throws RemoteException, NotBoundException
    {
        new ForgettingFactor();
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
    private void calculateLinearFunction() throws RemoteException {
        float sumLeaderSquares = 0;
        float sumLeader = 0;
        float sumFollower = 0;
        float sumLeaderFollowerProduct = 0;
        float leaderPrice;
        float followerPrice;
        float weight;

        for (int day = 0; day < recordDataList.size(); day++) {
            weight = calculateWeight(day);
            leaderPrice = recordDataList.get(day).m_leaderPrice;
            followerPrice = recordDataList.get(day).m_followerPrice;
            sumLeaderSquares += weight * leaderPrice*leaderPrice;
            sumLeader += weight * leaderPrice;
            sumFollower += weight * followerPrice;
            sumLeaderFollowerProduct += weight * leaderPrice*followerPrice;
        }

        coefficientA = (sumLeaderSquares*sumFollower - sumLeader*sumLeaderFollowerProduct)
                / (currentDayNumber*sumLeaderSquares - sumLeader*sumLeader);
        coefficientB = (currentDayNumber*sumLeaderFollowerProduct - sumLeader*sumFollower)
                / (currentDayNumber*sumLeaderSquares - sumLeader*sumLeader);

    }

    private float calculateWeight(int day) {
        int daysOutsideWindow = currentDayNumber - WINDOW_SIZE;
        if (day < daysOutsideWindow) {
            double increment = 0.01 / daysOutsideWindow;
            return (float)(0.98 + day * increment);
        } else {
            return 1;
        }
    }

    private float calculateLeaderPrice() {
        return (float)(( -3 - 0.3*coefficientA + 0.3*coefficientB)
                / (2* ( 0.3*coefficientB - 1)));
    }

    @Override
    public void endSimulation() throws RemoteException {
        System.out.println("Finished simulation.");
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