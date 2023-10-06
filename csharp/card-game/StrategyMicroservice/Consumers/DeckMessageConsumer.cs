using MassTransit;
using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Messages;
using Strategy;

namespace Consumers;

public class DeckMessageConsumer : IConsumer<DeckMessage>
{
	//private readonly IPublishEndpoint _endpoint;
	private readonly ICardPickStrategy _strategy = new Trivial();

	//public DeckMessageConsumer(IPublishEndpoint publishEndpoint) 
	//{
		//_endpoint = publishEndpoint;
	//}
	
	public async Task Consume(ConsumeContext<DeckMessage> context)
		=> await context.Publish<PickMessage>(new() 
				{
					id = context.Message.id,
					pick = _strategy.Pick(context.Message.deck)
				});
}