namespace ColliseumRunner;

public class ColliseumExperimentWorker
{
	ColliseumSandbox _sandbox;
	
	public ColliseumExperimentWorker(ColliseumSandbox sandbox) => _sandbox = sandbox;
	
	public void runExperiment() {
		int count = 0;
		for (int i = 0; i < 1000000; i++)
			count += _sandbox.runExperiment() ? 1 : 0;
		
		Console.WriteLine("Success rate is {0}", (double) count/1000000);	
	}
}
