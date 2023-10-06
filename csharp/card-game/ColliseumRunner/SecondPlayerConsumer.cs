using MassTransit;
using Nsu.ColiseumProblem.Contracts.Cards;
using Nsu.ColiseumProblem.Contracts.Messages;

namespace ColliseumRunner;

public class SecondPlayerConsumer : IConsumer<PickMessage>, IFirstPlayer {
	private readonly ISendEndpoint _sendEndpoint;
	private readonly Uri _playerUri = new("queue:zucc");
	private int _currentPick { get; set; } = -1;
	private int _currentMessageId { get; set; } = -1;

	public SecondPlayerConsumer(ISendEndpointProvider sendEndpointProvider)
	{
		_sendEndpoint = sendEndpointProvider.GetSendEndpoint(_playerUri).Result;
	}

	public int Pick(Card[] deck) {
		int pick = -1;
		lock(this) 
		{
			_currentPick = -1;
			_sendEndpoint.Send(new DeckMessage(){id = _currentMessageId, deck = deck}).Wait();
			if (_currentPick == -1)
				Monitor.Wait(this);

			_currentMessageId--;
			pick = _currentPick;
			_currentPick = -1;
		}

		return pick;
	}
	
	public Task Consume(ConsumeContext<PickMessage> context)
  {
		lock(this)
		{
			if (_currentMessageId == context.Message.id)
				return Task.CompletedTask;

			_currentPick = context.Message.pick;
			Monitor.Pulse(this);
		}
  	return Task.CompletedTask;
  }
}
