char** gen_topo_map (struct 3d_coord* point_cloud, int width, int height, int up_cutoff, int down_cutoff)
{
	char** ret = (char**)malloc (width * sizeof (char*));
	int** 2d_map = (int**)malloc (width * sizeof (int*));
	int i;
	int j;
	struct 3d_coord* current;

	//Initialize 2d map and topographic map
	for (i = 0; i < width; i ++)
	{
		ret [i] = (char*)malloc (height * sizeof (char));
		2d_map [i] = (int*)malloc (height * sizeof (int));

		for (j = 0; j < height; j ++)
		{
			ret [i][j] = 0;
			2d_map [i][j] = 0;
		}
	}

	//"Plot" the point cloud points on the 2d map
	current = point_cloud;
	while (current)
	{
		2d_map [current->x][current->y] = current->z;
		current = current->next;
	}

	//Insert some interpolation code here?

	//Turn the 2d map into a simple topographic map
	for (i = 0; i < width; i ++)
	{
		for (j = 0; j < height; j ++)
		{
			if (2d_map [i][j] > up_cutoff)
				ret [i][j] = 1;
			else if (2d_map [i][j] < down_cutoff)
				ret [i][j] = -1;
		}
	}

	return ret;
}
