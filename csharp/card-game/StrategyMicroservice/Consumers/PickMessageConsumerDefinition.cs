namespace Microservice.Consumers;

using MassTransit;

public class PickMessageConsumerDefinition :
        ConsumerDefinition<PickMessageConsumer>
{
	protected override void ConfigureConsumer(IReceiveEndpointConfigurator endpointConfigurator, 
			IConsumerConfigurator<PickMessageConsumer> consumerConfigurator)
  {
  	endpointConfigurator.UseMessageRetry(r => r.Intervals(500, 1000));
  }
}
