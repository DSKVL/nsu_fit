namespace Microservice.Consumers;

using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using System.Threading.Tasks;
using MassTransit;
using Nsu.ColiseumProblem.Contracts.Messages;
using Nsu.ColiseumProblem.Contracts.Cards;

public class PickMessageConsumer : IConsumer<PickMessage>
{
	private readonly DeckMessageConsumer _deckMessageConsumer;
	private PickSource _source;
	public int _currentColor { get; set; } = 0;
	public long _latestRecievedId { get; set; } = 0;
	private readonly ILogger<PickMessageConsumer> _logger;
	
	public PickMessageConsumer(DeckMessageConsumer deckMessageConsumer,
														 ILogger<PickMessageConsumer> logger,
														 IConfiguration configuration)
	{
		_deckMessageConsumer = deckMessageConsumer;
		_logger = logger;
		_source = configuration["source"] == "elon" ? 
			PickSource.Elon : PickSource.Zucc;
	}

	public Task Consume(ConsumeContext<PickMessage> context)
  {
		(long id, PickSource source, int pick) = context.Message;

		lock(_deckMessageConsumer) 
		{		
			_logger.LogInformation($"Received pick with id {id}, value {pick}, current deck id is {_deckMessageConsumer._currentDeckMessage.id}"); 
				
			if (source == _source) 
			{
				_logger.LogInformation("It was my pick. Ignore.");
				return Task.CompletedTask; 
			}

			while (id != _deckMessageConsumer._currentDeckMessage.id)
			{
				_logger.LogInformation("Awaiting deck message with same id");
				Monitor.Wait(_deckMessageConsumer);
			}

			_logger.LogInformation($"Other player's pick. {pick}");

			_currentColor = _deckMessageConsumer._currentDeckMessage
																					.deck[pick].Color == CardColor.Black ? 0 : 1;
		}
		
		lock(this)
		{
			_latestRecievedId = id;
			_logger.LogInformation($"Pulsing");
			Monitor.Pulse(this);
		}

		return Task.CompletedTask;
	} 
}
