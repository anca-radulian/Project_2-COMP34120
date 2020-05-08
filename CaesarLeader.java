import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

final class CaesarLeader extends PlayerImpl {

    private final static int HISTORY_RECORDS = 100;
    private List<Record> recordDataList;
    private float coefficientA;
    private float coefficientB;
    private float leaderPublishedPrice;
		private float totalProfit = 0;

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
        initialiseHistoryData();
        calculateLinearFunction();
        leaderPublishedPrice = calculateLeaderPrice();
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

        for (Record record : recordDataList) {
            float leaderPrice = record.m_leaderPrice;
            float followerPrice = record.m_followerPrice;
            sumLeaderSquares += leaderPrice*leaderPrice;
            sumLeader += leaderPrice;
            sumFollower += followerPrice;
            sumLeaderFollowerProduct += leaderPrice*followerPrice;
        }

        coefficientA = (sumLeaderSquares*sumFollower - sumLeader*sumLeaderFollowerProduct)
                            / (HISTORY_RECORDS*sumLeaderSquares - sumLeader*sumLeader);
        coefficientB = (HISTORY_RECORDS*sumLeaderFollowerProduct - sumLeader*sumFollower)
                            / (HISTORY_RECORDS*sumLeaderSquares - sumLeader*sumLeader);

    }

    private float calculateLeaderPrice() {
        return (float) (( -3 - 0.3*coefficientA + 0.3*coefficientB)
                        / 2* ( 0.3*coefficientB - 1));
    }

   private float demand(final float leaderPrice, final float followerPrice) {
		 return 2 - leaderPrice + 0.3 * followerPrice;
   }

	 private float calculateProfit(final float leaderPrice, final float followerPrice) {
		 	return (leaderPrice - 1)* demand(leaderPrice, followerPrice)
	 }

    @Override
    public void endSimulation() throws RemoteException {

			System.out.println("Total profit: " + calculateProfit);
        super.endSimulation();
        //TO DO: delete the line above and put your own finalization code here
    }

    @Override
    public void proceedNewDay(int p_date) throws RemoteException {
        m_platformStub.publishPrice(PlayerType.LEADER, leaderPublishedPrice);

				Record newRecord = m_platformStub.query(PlayerType.LEADER, p_date);

				totalProfit += calculateProfit(newRecord.m_leaderPrice, newRecord.m_followerPrice);




        /*
         * Check for new price
         * Record l_newRecord = m_platformStub.query(m_type, p_date);
         *
         * Your own math model to compute the price here
         * ...
         * float l_newPrice = ....
         *
         * Submit your new price, and end your phase
         * m_platformStub.publishPrice(m_type, l_newPrice);
         */
    }
}
