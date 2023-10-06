using Microsoft.Extensions.Hosting;
using MassTransit;
using Consumers;

class Program {

	public static void Main(string[] args) {
		var builder = Host.CreateDefaultBuilder(args);

		builder.ConfigureServices((hostContext, services) => 
				{
					services.AddMassTransit(x => 
					{
						x.AddConsumer<DeckMessageConsumer>();

						x.UsingRabbitMq((context, cfg) =>
    				{
							cfg.ReceiveEndpoint($"{args[0]}", e => 
							{
								e.ConfigureConsumer<DeckMessageConsumer>(context);
							});

							cfg.ConfigureEndpoints(context);
    				});	
					});
				});
		var app = builder.Build();
		app.Run();
	}
}
