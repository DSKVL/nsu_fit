using Microsoft.AspNetCore.Mvc;
using StubServer.Data;
using StubServer.Data.UInt32;

namespace StubServer.Controllers;

[ApiController]
[Route("[controller]")]
public class HandshakeController : ControllerBase
{
    [HttpGet(Name = "Handshake")]
    public IEnumerable<Parameter> Get()
    {
        return Enumerable.Range(0, 3).Select(index => new Parameter()
						{
							name = $"Parameter{index}",
							type = ParameterType.unsignend_int,
							value = new UInt32Value() { value = (uint) index*10 },
							range = new UInt32Range()
							});
    }
}
