

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {workers: []};
    }

    componentDidMount() {
        client({method: 'GET', path: 'api/workers'}).done(response => {
                this.setState({workers: response.entity._embedded.workers});
            });
    }

    render() {
        return(
            <WorkerList workers={this.state.workers}/>
        )
    }
}

class WorkerList extends React.Component {
    render() {
        const workers = this.props.workers.map(worker =>
            <Worker key={worker._links.self.href} worker={worker}/>
        );
        return (
            <table>
                <tbody>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                </tr>
                {workers}
                </tbody>
            </table>
        )
    }
}

class Worker extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.worker.firstName}</td>
                <td>{this.props.worker.lastName}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)