namespace ColliseumRunner;  

using System.Net.Http.Json;
using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

public abstract class StrategyClient : ICardPickStrategy 
{
	private HttpClient _client;

	protected StrategyClient(string clientName, IHttpClientFactory clientFactory) {
		_client = clientFactory.CreateClient(clientName);
	}

	public int Pick(Card[] deck) 
	{
		var request = new HttpRequestMessage();
		request.Content = JsonContent.Create(deck);
		var response = _client.Send(request);

		int pick = response.Content.ReadFromJsonAsync<int>().Result;
		Console.WriteLine($"service picked {pick}");
		return pick;	
	}	
}

