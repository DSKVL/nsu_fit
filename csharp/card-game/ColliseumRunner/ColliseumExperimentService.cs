namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;

public class ColliseumExperimentService : BackgroundService
{
	ColliseumSandbox _sandbox;
	
	public ColliseumExperimentService(ColliseumSandbox sandbox) => _sandbox = sandbox;
	
	override protected Task ExecuteAsync(CancellationToken token) {
		new ColliseumExperimentWorker(_sandbox).runExperiment();
		return Task.CompletedTask;
	}
}
