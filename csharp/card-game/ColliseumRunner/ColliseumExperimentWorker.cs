namespace ColliseumRunner;

using Microsoft.Extensions.Configuration;

public class ColliseumExperimentWorker
{
	private readonly ColliseumSandbox _sandbox;
	private readonly int totalExperiments;
	private readonly int DEFAULT_TOTAL_EXPERIMENTS = 1000000;

	public ColliseumExperimentWorker(
			ColliseumSandbox sandbox,
			IConfiguration configuration) 
	{
		_sandbox = sandbox;
		var s = configuration["experiments"];
		try 
		{
			totalExperiments = s != null ? Int32.Parse(s) : DEFAULT_TOTAL_EXPERIMENTS;	
		} 
		catch (FormatException)
		{
			totalExperiments = DEFAULT_TOTAL_EXPERIMENTS;	
		}
	}
	
	public void runExperiment() {
		int count = 0;
		for (int i = 0; i < totalExperiments; i++)
			count += _sandbox.runExperiment() ? 1 : 0;
		
		Console.WriteLine("Success rate is {0}", (double) count/totalExperiments);	
	}
}
