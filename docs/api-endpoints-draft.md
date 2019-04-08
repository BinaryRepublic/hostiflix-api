### MODELS

```
Project {
	id: String,
	hash: String,
	name: String,
	type: ENUM ( NODEJS ),
	startCode: String,
	buildCode: String,
	repositoryOwner: String,
	repositoryName: String,
	branches: [ Branch ],
	createdAt: Datetime
}

Branch {
	id: String,
	name: String,
	subDomain: String,
	jobs: [ Job ]
}

Job {
	id: String,
	status: ENUM ( BUILD_SCHEDULED; BUILD_CANCELLED; DEPLOYMENT_PENDING; DEPLOYMENT_SUCCESSFUL; DEPLOYMENT_FAILED ),
	createdAt: DateTime,
	finishedAt: DateTime
}

GithubRepo {
	id: String,
	name: String
	defaultBranch: String
	owner: String
}

GithubBranch {
	name: String
}
```

### ENDPOINTS
```
GET /projects

res: HTTP 200 {
	"projects": [ Project ]
}
```

```
GET /projects/{id}

res: HTTP 200 {
	Project
}
```

```
GET /projects/hash

res: HTTP 200 {
	"hash": "k24"
}
```

```
POST /projects

req: {
	Project
}

res: HTTP 201 {
	Project
}
```

```
GET /github/repos

res: HTTP 200 {
	"repos": [ GithubRepo ]
}
```

```
GET /github/repos/{repoOwner}/{repoName}/branches

res: HTTP 200 {
	"branches": [ GithubBranch ]
}
```

```
PUT /jobs/{id}/status/{newStatus}

res: HTTP 204
```
