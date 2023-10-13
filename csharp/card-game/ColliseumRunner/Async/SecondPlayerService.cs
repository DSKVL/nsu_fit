using MassTransit;

namespace ColliseumRunner.Async;

public class SecondPlayerService : StrategyClient 
{
	public SecondPlayerService(IHttpClientFactory clientFactory,
			ISendEndpointProvider provider) 
		: base("zucc", clientFactory, provider) 
	{}
}
