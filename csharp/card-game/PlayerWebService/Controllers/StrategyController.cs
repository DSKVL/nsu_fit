using Microsoft.AspNetCore.Mvc;
using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;
using Strategy;


namespace PlayerWebService.Controllers;

[ApiController]
[Route("[controller]")]
public class StrategyController : ControllerBase
{
	private readonly ICardPickStrategy _strategy = new Trivial();

  [HttpGet(Name = "GetPick")]
  public int Get(Card[] deck) => _strategy.Pick(deck);
	
}
