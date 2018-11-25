import requests

url = 'https://launchpad.espooinnovationgarden.fi/_ah/api/company/v1/corporates'

resp = requests.get(url=url, verify=False)
data = resp.json() # Check the JSON Response Content documentation
