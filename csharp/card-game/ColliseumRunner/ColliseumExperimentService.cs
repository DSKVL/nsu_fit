namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Configuration;

public class ColliseumExperimentService : BackgroundService
{
	private readonly IColliseumSandbox _sandbox;
	private readonly IConfiguration _configuraion;

	public ColliseumExperimentService(IColliseumSandbox sandbox,
			IConfiguration configuration)
	{ 
		_sandbox = sandbox;
		_configuraion = configuration;
	}
	
	override protected Task ExecuteAsync(CancellationToken token) 
	{
		new ColliseumExperimentWorker(_sandbox, _configuraion).runExperiment();
		return Task.CompletedTask;
	}
}
