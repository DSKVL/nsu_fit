namespace ColliseumRunner.Async;  

using System.Net.Http.Json;
using Nsu.ColiseumProblem.Contracts.Cards;
using Nsu.ColiseumProblem.Contracts.Messages;
using MassTransit;

public abstract class StrategyClient : ICardPickStrategyAsync
{
	private readonly HttpClient _client;
	private readonly ISendEndpointProvider _sendEndpointProvider;
	private readonly Uri _playerUri;
	private long _currentMessageId { get; set; } = 0;
	
	protected StrategyClient(string clientName,
			IHttpClientFactory clientFactory, 
			ISendEndpointProvider provider)
	{
		_client = clientFactory.CreateClient(clientName);
		_sendEndpointProvider = provider;
		_playerUri = new($"queue:{clientName}-deck");
		Console.WriteLine($"My uri {_playerUri}");
	}

	public async Task<CardColor> Pick(Card[] deck) 
	{
		{	
			var endpoint = await _sendEndpointProvider.GetSendEndpoint(_playerUri);
			await endpoint.Send<DeckMessage>(new (++_currentMessageId, deck));
		}
		var request = new HttpRequestMessage(){ Method = HttpMethod.Post };
		Console.WriteLine($"Request send, id is {_currentMessageId}");
		var response = _client.Send(request);

		return response.Content.ReadFromJsonAsync<int>()
			.ContinueWith(colorString => colorString.Result == 1 ? CardColor.Red : CardColor.Black).Result;
	}	
}

