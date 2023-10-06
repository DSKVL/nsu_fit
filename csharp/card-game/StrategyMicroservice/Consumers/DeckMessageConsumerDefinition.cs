using MassTransit;

namespace Consumers;

public class DeckMessageConsumerDefinition : ConsumerDefinition<DeckMessageConsumer>
{
	protected override void ConfigureConsumer(IReceiveEndpointConfigurator endpointConfigurator, IConsumerConfigurator<DeckMessageConsumer> consumerConfigurator)
  {
  	endpointConfigurator.UseMessageRetry(r => r.Intervals(500, 1000));
  }
}