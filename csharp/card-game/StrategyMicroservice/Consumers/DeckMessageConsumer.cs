namespace Microservice.Consumers;

using System.Threading.Tasks;
using MassTransit;
using Strategy;
using Nsu.ColiseumProblem.Contracts.Messages;
using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

public class DeckMessageConsumer : IConsumer<DeckMessage>
{
	public DeckMessage _currentDeckMessage { get; set; } = new(0, new Card[0]);
	private readonly ICardPickStrategy _strategy = new Trivial();
	private PickSource _source;
	private readonly ILogger<DeckMessageConsumer> _logger;
	
	public DeckMessageConsumer(ILogger<DeckMessageConsumer> logger,
														 IConfiguration configuration)
	{
		_logger = logger;
		_source = configuration["source"] == "elon" ? 
			PickSource.Elon : PickSource.Zucc;
	}

	public Task Consume(ConsumeContext<DeckMessage> context)
  {	
		var deckMessage = context.Message;
		var id = deckMessage.id;
		_logger.LogInformation($"Received deck with id {id} ");
		lock(this)
		{
			_currentDeckMessage = deckMessage;
			Monitor.Pulse(this);
		}

   	context.Publish<PickMessage>(new(
				id,
				_source,
				_strategy.Pick(deckMessage.deck)
		)).Wait();

		return Task.CompletedTask;
  }
}