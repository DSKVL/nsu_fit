namespace Microservice.Controllers;

using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Microservice.Consumers;

[ApiController]
[Route("strategy")]
public class StrategyController : ControllerBase
{
	public PickMessageConsumer _pickMessageConsumer;
	public string _currentColor { get; set; }= "";
	public long _latestSendId { get; set; } = 0;
	public long _latestRecievedId { get; set; } = 0;
	private readonly ILogger<StrategyController> _logger;
	
	public StrategyController(PickMessageConsumer pickMessageConsumer,
														ILogger<StrategyController> logger)
	{
		_pickMessageConsumer = pickMessageConsumer;
		_logger = logger;
	}
	
  
	[HttpPost(Name = "color")]
	public int Post() 
	{
		_logger.LogInformation("Trying send");
		int currentColor;
		lock(_pickMessageConsumer)
		{
			if (_pickMessageConsumer._latestRecievedId == _latestSendId)	
			{
				_logger.LogInformation("Yet no new picks. Waiting.");
				Monitor.Wait(_pickMessageConsumer);
				_logger.LogInformation("Awake");
			}
			_latestSendId = _pickMessageConsumer._latestRecievedId;
			currentColor = _pickMessageConsumer._currentColor;
		}
		_logger.LogInformation("Sending color");
		return currentColor;
	}
}
