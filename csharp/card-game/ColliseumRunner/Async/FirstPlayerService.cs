using MassTransit;

namespace ColliseumRunner.Async;

public class FirstPlayerService : StrategyClient 
{
	public FirstPlayerService(IHttpClientFactory clientFactory, 
			ISendEndpointProvider provider) 
		: base("elon", clientFactory, provider) 
	{}
}
