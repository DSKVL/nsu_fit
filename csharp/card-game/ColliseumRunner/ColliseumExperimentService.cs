namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Configuration;

public class ColliseumExperimentService : BackgroundService
{
	private readonly ColliseumSandbox _sandbox;
	private readonly IConfiguration _configuraion;

	public ColliseumExperimentService(
			ColliseumSandbox sandbox,
			IConfiguration configuration)
	{ 
		_sandbox = sandbox;
		_configuraion = configuration;
	}
	
	override protected Task ExecuteAsync(CancellationToken token) {
		new ColliseumExperimentWorker(_sandbox, _configuraion).runExperiment();
		return Task.CompletedTask;
	}
}
