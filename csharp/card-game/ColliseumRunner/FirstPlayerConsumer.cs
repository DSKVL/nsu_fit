using MassTransit;
using Nsu.ColiseumProblem.Contracts.Cards;
using Nsu.ColiseumProblem.Contracts.Messages;

namespace ColliseumRunner;

public class FirstPlayerConsumer : IConsumer<PickMessage>, IFirstPlayer {
	private readonly ISendEndpoint _sendEndpoint;
	private readonly Uri _playerUri = new("queue:elon");
	private int _currentPick { get; set; } = -1;
	private int _currentMessageId { get; set; } = 0;

	public FirstPlayerConsumer(ISendEndpointProvider sendEndpointProvider) {
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

			_currentMessageId++;
			pick = _currentPick;
			_currentPick = -1;
		}
		Console.WriteLine("We have a pick!");
		
		return pick;
	}
	
	public Task Consume(ConsumeContext<PickMessage> context)
  {
		Console.WriteLine("I've recieved!");
		lock(this)
		{
			if (_currentMessageId != context.Message.id)
				return Task.CompletedTask;

			_currentPick = context.Message.pick;
			Monitor.Pulse(this);
		}
  	return Task.CompletedTask;
  }
}
