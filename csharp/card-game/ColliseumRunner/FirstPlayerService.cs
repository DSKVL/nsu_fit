namespace ColliseumRunner;

public class FirstPlayerService : StrategyClient {
	public FirstPlayerService(IHttpClientFactory clientFactory) : base("p1", clientFactory) {}
}
