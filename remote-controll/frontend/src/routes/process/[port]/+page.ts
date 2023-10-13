import type { PageLoad } from './$types';

export const load = (async ({ params, fetch }) => {
	
	let handshake = await fetch(`localhost:${params.port}/handshake`);

	return handshake
}) satisfies PageLoad;
