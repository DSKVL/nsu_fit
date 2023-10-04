namespace ColliseumRunner;

public class SecondPlayerService : StrategyClient {
	public SecondPlayerService(IHttpClientFactory clientFactory) : base("p2", clientFactory) {}
}
