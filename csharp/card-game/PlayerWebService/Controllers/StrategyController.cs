using Microsoft.AspNetCore.Mvc;
using Nsu.ColiseumProblem.Contracts.Cards;
using Strategy;

namespace PlayerWebService.Controllers;

[ApiController]
[Route("[controller]")]
public class StrategyController : ControllerBase
{
    [HttpGet(Name = "GetPick")]
    public int Get(Card[] deck) => new Trivial().Pick(deck);
}
