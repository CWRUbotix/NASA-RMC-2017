struct coord
{
	int x;
	int y;

	struct coord* next;
	struct coord* prev;
}

struct 3d_coord
{
	int x;
	int y;
	int z;

	struct coord* next;
	struct coord* prev;
}
